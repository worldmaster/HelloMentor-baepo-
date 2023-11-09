package com.kh.hellomentor.chat.model.repo;

import com.kh.hellomentor.chat.model.dao.ChatDao;
import com.kh.hellomentor.chat.model.vo.ChatMessageDTO;
import com.kh.hellomentor.chat.model.vo.ChatRoomDTO;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@Slf4j
public class ChatRoomRepository {


    @Autowired
    private ChatDao chatDao;

    public List<ChatRoomDTO> findAllRooms(int userNo) {
        List<ChatRoomDTO> result = chatDao.findAllRooms(userNo);
        return result;
    }

    public ChatRoomDTO findRoomById(String id) {return chatDao.findRoomById(id);}

    public List<ChatMessageDTO> findMessageById(String id) {
        return chatDao.findMessageById(id);
    }

    public ChatRoomDTO createStudyChatRoomDTO(int postNo) {
        String name = chatDao.findStudyTitle(postNo);
        ChatRoomDTO room = ChatRoomDTO.createStudyRoom(name, postNo);
        chatDao.createStudyRoom(room);

        return room;
    }

    public void createChatRoomDTO(int mentorNo, int menteeNo) {
        Mentoring men = chatDao.findMentorTitle(mentorNo);
        ChatRoomDTO room = ChatRoomDTO.createRoom(men.getTitle(), men.getRegisNo());
        log.info("room : {}", room);
        chatDao.createMentoringRoom(room, mentorNo, menteeNo);

    }

    public void insertMessage(ChatMessageDTO message) {
        chatDao.insertMessage(message);
    }
}
