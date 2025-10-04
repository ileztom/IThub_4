
package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import java.io.File

class ApplicationTest {
    @BeforeTest
    fun setup() {
        // ensure data dir and files are clean for tests
        val dataDir = File(System.getProperty("user.dir"), "data")
        if (!dataDir.exists()) dataDir.mkdirs()
        File(dataDir, "posts.json").writeText("[]")
        File(dataDir, "users.json").writeText("[]")
        File(dataDir, "comments.json").writeText("[]")
    }

    @Test
    fun testRootAndSwagger() = testApplication {
        application { module() }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Swagger UI"))
    }

    @Test
    fun testUserCrud() = testApplication {
        application { module() }
        val createResp = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("{"name":"Alice","email":"a@x.com","role":"admin"}")
        }
        assertEquals(HttpStatusCode.Created, createResp.status)
        val created = createResp.bodyAsText()
        assertTrue(created.contains("Alice"))

        val listResp = client.get("/users")
        assertEquals(HttpStatusCode.OK, listResp.status)
        assertTrue(listResp.bodyAsText().contains("Alice"))
    }
}
