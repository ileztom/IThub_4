package com.example.recommendation

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import com.fasterxml.jackson.databind.*
import io.ktor.serialization.jackson.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.avg
import java.time.Duration
import java.util.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import kotlinx.coroutines.*

fun main() {

    Database.connect(
        url = "jdbc:postgresql://postgres:5432/users",
        driver = "org.postgresql.Driver",
        user = "demo",
        password = "demo"
    )

    transaction {
        create(Ratings)
    }

    // Запускаем Kafka Consumer в корутине
    GlobalScope.launch {
        startKafkaConsumer()
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }

        routing {
            get("/recommendations/{userId}") {
                val userId = call.parameters["userId"]?.toLongOrNull()
                if (userId == null) {
                    call.respond(emptyList<RecommendationDto>())
                    return@get
                }

                val avgScore = Ratings.score.avg()

                val recommendations = transaction {
                    Ratings
                        .slice(Ratings.courseId, avgScore)
                        .select { Ratings.userId eq userId }
                        .groupBy(Ratings.courseId)
                        .orderBy(avgScore, SortOrder.DESC)
                        .limit(5)
                        .map {
                            RecommendationDto(
                                courseId = it[Ratings.courseId],
                                avgScore = it[avgScore] ?: 0.0
                            )
                        }
                }

                call.respond(recommendations)
            }
        }
    }.start(wait = true)
}


object Ratings : Table("ratings_for_recommendation") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val courseId = long("course_id")
    val score = integer("score")
    override val primaryKey = PrimaryKey(id)
}

data class RatingEvent(val userId: Long, val courseId: Long, val score: Int)
data class RecommendationDto(val courseId: Long, val avgScore: Number)


fun startKafkaConsumer() {
    val props = Properties()
    props["bootstrap.servers"] = "kafka:9092"
    props["group.id"] = "recommendation-service"
    props["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
    props["value.deserializer"] = "org.apache.kafka.common.serialization.ByteArrayDeserializer"
    props["auto.offset.reset"] = "earliest"

    val consumer = KafkaConsumer<String, ByteArray>(props)
    consumer.subscribe(listOf("ratings"))

    while (true) {
        val records = consumer.poll(Duration.ofSeconds(1))
        for (record in records) {
            val json = String(record.value())
            try {
                val mapper = ObjectMapper()
                val event = mapper.readValue(json, RatingEvent::class.java)

                transaction {
                    Ratings.insert {
                        it[userId] = event.userId
                        it[courseId] = event.courseId
                        it[score] = event.score
                    }
                }

            } catch (e: Exception) {
                println("Failed to process rating event: $json")
            }
        }
    }
}
