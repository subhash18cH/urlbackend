package com.subhash.urlbackend.controller;

import com.subhash.urlbackend.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/url")
public class UrlMappingController {

    @Autowired
    private UrlMappingService urlService;

    @PostMapping("/shortUrl")
    public String convertToShortUrl(@RequestParam String LongUrl) throws URISyntaxException, NoSuchAlgorithmException {
        return urlService.shortenUrl(LongUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl = urlService.getLongUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    @DeleteMapping("/delete-url")
    public void deleteUrl(@RequestParam String shortUrl){
        urlService.removeUrl(shortUrl);
    }
}
