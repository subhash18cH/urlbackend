package com.subhash.urlbackend.service;

import com.subhash.urlbackend.model.Userr;
import com.subhash.urlbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Userr findByUserName(String username) {
        Optional<Userr> user=userRepository.findByUserName(username);
        return user.orElseThrow(()-> new RuntimeException("User not found with Username:"+ username));
    }

    public Optional<Userr> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Userr registerUser(Userr user) {
        if(user.getPassword()!= null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}
