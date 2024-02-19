package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.user.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDocumentDao extends JpaRepository<UserDocument,String> {
}
