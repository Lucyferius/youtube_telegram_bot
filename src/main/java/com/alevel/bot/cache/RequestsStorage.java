package com.alevel.bot.cache;

import com.alevel.bot.model.dto.Request;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RequestsStorage {

    private final Map<Long, Request> requests = new ConcurrentHashMap<>();

    public void addRequest(Long chatId, Request request){
        requests.put(chatId,request);
    }
    public void updateRequest(Long chatId, Request request){
        requests.put(chatId,request);
    }
    public void removeRequest(Long chatId){
        requests.remove(chatId);
    }
    public Request getCurrentRequest(Long chatId){
        return requests.get(chatId);
    }
    public boolean isRequestPresent(Long chatId){
        return requests.containsKey(chatId);
    }
}
