package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}