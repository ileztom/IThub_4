package com.example.api.controller;

import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository){this.userRepository = userRepository;}

    @GetMapping
    public List<User> getAll(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size){
        return userRepository.findAll(PageRequest.of(page,size)).getContent();
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable Long id){
        Optional<User> u = userRepository.findById(id);
        return u.orElseGet(() -> { return (User) java.util.Map.of("error","User not found");});
    }

    @PostMapping
    public User create(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody User newUser){
        return userRepository.findById(id).map(u -> {
            u.setName(newUser.getName());
            u.setEmail(newUser.getEmail());
            if(newUser.getPassword()!=null) u.setPassword(newUser.getPassword());
            return userRepository.save(u);
        }).orElseGet(() -> (User) java.util.Map.of("error","User not found"));
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id){
        return userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return java.util.Map.of("status","deleted");
        }).orElseGet(() -> java.util.Map.of("error","User not found"));
    }
}
