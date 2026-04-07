package com.adrian.notificationservice.service.interf;

import com.adrian.notificationservice.dto.request.ConfirmMailRequest;
import com.adrian.notificationservice.dto.request.FailedMailRequest;

public interface IMailService {

    void sendConfirmMail(ConfirmMailRequest request);

    void sendFailedMail(FailedMailRequest request);

    void sendWelcomeMail(String username, String email);

}
