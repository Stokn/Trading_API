package com.vulturi.trading.api.services.kyc;

import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserDocument;

import java.util.Collection;

public interface UserDocumentService {
    void save(User user, UserDocument userDocument);
    Collection<UserDocument> get(String userId);
}
