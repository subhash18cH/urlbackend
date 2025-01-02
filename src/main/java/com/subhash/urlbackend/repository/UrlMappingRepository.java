package com.subhash.urlbackend.repository;


import com.subhash.urlbackend.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping,Integer> {
    UrlMapping findByLongUrl(String longUrl);

    UrlMapping findByShortUrl(String shortUrl);

}
