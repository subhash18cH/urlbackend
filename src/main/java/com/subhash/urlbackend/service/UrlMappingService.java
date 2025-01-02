package com.subhash.urlbackend.service;


import com.subhash.urlbackend.model.UrlMapping;
import com.subhash.urlbackend.model.UserInfo;
import com.subhash.urlbackend.repository.UrlCacheRepository;
import com.subhash.urlbackend.repository.UrlMappingRepository;
import com.subhash.urlbackend.repository.UserInfoRepository;
import com.subhash.urlbackend.utility.GeoData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

@Service
public class UrlMappingService {

    @Autowired
    private UrlMappingRepository repository;

    @Autowired
    private UrlCacheRepository cacheRepository;

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    public String shortenUrl(String longUrl) throws URISyntaxException {

        validateUrl(longUrl);

        String cachedShortUrl= cacheRepository.getShortUrlFromCache(longUrl);
        if (cachedShortUrl != null) {
            return  cachedShortUrl;
        }
        UrlMapping existingUrl=repository.findByLongUrl(longUrl);
        if(existingUrl != null){
            cacheRepository.saveToCache(existingUrl.getShortUrl(),longUrl);
            return existingUrl.getShortUrl();
        }
        String shortUrl = generateShortUrl();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        urlMapping.setShortUrl(shortUrl);
        try {
            repository.save(urlMapping);
            cacheRepository.saveToCache(shortUrl, longUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save URL mapping", e);
        }
        return shortUrl;
    }

    private String generateShortUrl() {
        long id = System.currentTimeMillis();
        return generateBase62(id, 7);
    }

    private void validateUrl(String longUrl) throws URISyntaxException {
        new URI(longUrl);
    }

    public String getLongUrl(String shortUrl) {

        String cachedLongUrl= cacheRepository.getLongUrlFromCache(shortUrl);

        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();

        // for production use
//        String ipAddress=getClientIp(request);
        String deviceType = userAgent.contains("Mobile") ? "Mobile" : "Desktop";

        if (cachedLongUrl != null) {
            updateUserInfo(shortUrl, ipAddress, deviceType);
            return cachedLongUrl;
        }
        UrlMapping urlMapping = repository.findByShortUrl(shortUrl);

        if (urlMapping != null) {
            updateUserInfo(shortUrl, ipAddress, deviceType);
            cacheRepository.saveToCache(shortUrl, urlMapping.getLongUrl());
            return urlMapping.getLongUrl();
        }
        throw new RuntimeException("URL not found");
    }

    //for production use to get ip address
//    public String getClientIp(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip != null && !ip.isEmpty()) {
//            return ip.split(",")[0];
//        }
//        return request.getRemoteAddr();
//    }

    private void updateUserInfo(String shortUrl, String ipAddress, String deviceType) {
        UserInfo userInfo = userRepository.findByShortUrlAndIpAddress(shortUrl, ipAddress);

        GeoDataService geoService = new GeoDataService();
        GeoData geoData = geoService.getGeoData(ipAddress);

        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setClickCount(1);
            userInfo.setShortUrl(shortUrl);
            userInfo.setIpAddress(ipAddress);
            userInfo.setDeviceType(deviceType);
            userInfo.setAccessTime(LocalDateTime.now());
        } else {
            userInfo.setClickCount(userInfo.getClickCount() + 1);
            userInfo.setAccessTime(LocalDateTime.now());
        }
        if (geoData != null) {
            userInfo.setCity(geoData.getCity());
            userInfo.setRegion(geoData.getRegion());
            userInfo.setCountry(geoData.getCountry());
        }
        userRepository.save(userInfo);
    }

    private String generateBase62(long number, int length) {
        final String base62Chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortUrl = new StringBuilder();

        while (number > 0) {
            shortUrl.append(base62Chars.charAt((int) (number % 62)));
            number /= 62;
        }
        while (shortUrl.length() < length) {
            shortUrl.append(base62Chars.charAt((int) (Math.random() * 62)));
        }
        return shortUrl.reverse().toString();
    }

    private boolean isValidShortUrl(String shortUrl) {
        if (shortUrl == null || shortUrl.isEmpty()) {
            return false;
        }
        if (shortUrl.length() < 7 || shortUrl.length() > 10) {
            return false;
        }
        return true;

//        String regex = "^[a-zA-Z0-9_-]+$";
//        return shortUrl.matches(regex);
    }


    public void removeUrl(String shortUrl) {

        if (!isValidShortUrl(shortUrl)) {
            System.out.println("Invalid short URL format.");
            return;
        }

        String longUrl = cacheRepository.getLongUrlFromCache(shortUrl);
        if (longUrl != null) {
            cacheRepository.removeUrlFromCache(shortUrl, longUrl);
        } else {
            System.out.println("Short URL not found in cache.");
        }

        UrlMapping urlMapping=repository.findByShortUrl(shortUrl);
        if(urlMapping != null){
            repository.delete(urlMapping);
        }else {
            System.out.println("Short URL not found in database.");
        }

        UserInfo userInfo=userRepository.findByShortUrl(shortUrl);
        if(userInfo!=null){
            userRepository.delete(userInfo);
        }
        else{
            System.out.println("userInfo not found.");
        }
    }
}
