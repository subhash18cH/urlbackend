package com.subhash.urlbackend.repository;

import com.subhash.urlbackend.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {

    UserInfo findByShortUrl(String shortUrl);

    UserInfo findByShortUrlAndIpAddress(String shortUrl, String ipAddress);
}
