package com.llu1ts.shopapp.repo;

import com.llu1ts.shopapp.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
}