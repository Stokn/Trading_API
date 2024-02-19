package com.vulturi.trading.api.services.kyc;

import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserDocument;
import com.vulturi.trading.api.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private UserService userService;


    @Override
    public void save(User user, UserDocument userDocument) {
    }

    @Override
    public Collection<UserDocument> get(String userId) {
        return null;
    }
}
