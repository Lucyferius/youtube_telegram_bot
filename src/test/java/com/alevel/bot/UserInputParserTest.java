package com.alevel.bot;

import com.alevel.bot.service.message.UserInputParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class UserInputParserTest {

    @Autowired
    private UserInputParser parser;

    @Test
    void urlTest(){
        String validUrl_v1 = "https://www.youtube.com/watch?v=6366dxFf-Os&ab_channel=ArcticMonkeysVEVO";
        String validUrl_v2 = "https://youtu.be/c-Es4gkwGkY";
        String validUrl_v3 = "https://youtu.be/3G0F4HIkn5U";
        String invalidUrl_v1 = "https://www.w3.org/Style/Examples/007/fonts.ru.html";
        String invalidUrl_v2 = "https://you.be/c-Es4gkwGkY";
        String invalidUrl_v3 = "http/www.youtube.com/watch?=6366dxFf-Os&ab_channel=ArcticMonkeysVEVO";

        Assertions.assertEquals("6366dxFf-Os", parser.getYouTubeVideoId(validUrl_v1));
        Assertions.assertEquals("c-Es4gkwGkY", parser.getYouTubeVideoId(validUrl_v2));
        Assertions.assertEquals("3G0F4HIkn5U", parser.getYouTubeVideoId(validUrl_v3));

        Assertions.assertNull(parser.getYouTubeVideoId(invalidUrl_v1));
        Assertions.assertNull(parser.getYouTubeVideoId(invalidUrl_v2));
        Assertions.assertNull(parser.getYouTubeVideoId(invalidUrl_v3));
    }
}
