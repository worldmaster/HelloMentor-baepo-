package com.kh.hellomentor.chat.model.dao;

import com.kh.hellomentor.chat.model.vo.ChatMessageDTO;
import com.kh.hellomentor.chat.model.vo.ChatRoomDTO;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ChatDao {

    @Autowired
    private SqlSessionTemplate session;

    public List<ChatRoomDTO> findAllRooms(int userNo) {

        List<ChatRoomDTO> result = session.selectList("chatMapper.findAllRooms", userNo);

        return result;
    }

    public ChatRoomDTO findRoomById(String roomId) {
        return session.selectOne("chatMapper.findRoomById", roomId);
    }

    public List<ChatMessageDTO> findMessageById(String roomId) {
        List<ChatMessageDTO> result = session.selectList("chatMapper.findMessageById", roomId);
        for(ChatMessageDTO message : result) {
            log.info("message : {}", message);
        }
        return result;

//        return session.selectList("chatMapper.findMessageById", roomId);
    }

    public void insertMessage(ChatMessageDTO message) {
        session.insert("chatMapper.insertMessage", message);
    }

    public String findStudyTitle(int postNo) {
        return session.selectOne("chatMapper.findStudyTitle", postNo);
    }

    public void createStudyRoom(ChatRoomDTO room) {
        session.insert("chatMapper.createStudyRoom", room);
        int postNo = room.getRelatedNo();
        List<Integer> applicants = session.selectList("chatMapper.findStudyApplicants", postNo);
        Map<String, Object> data = new HashMap<>();
        data.put("roomId", room.getRoomId());

        for(int i = 0; i < applicants.size(); i++) {
            int userNo = applicants.get(i);
            data.put("userNo", userNo);
            session.insert("chatMapper.insertChatMember", data);
        }

    }

    public Mentoring findMentorTitle(int mentorNo) {
        return session.selectOne("chatMapper.findMentorTitle", mentorNo);
    }

    public void createMentoringRoom(ChatRoomDTO room, int mentorNo, int menteeNo) {
        session.insert("chatMapper.createMentoringRoom", room);
        List<Integer> applicants = new ArrayList<>();
        applicants.add(0, mentorNo);
        applicants.add(1,menteeNo);
        Map<String, Object> data = new HashMap<>();
        data.put("roomId", room.getRoomId());

        for(int i = 0; i < applicants.size(); i++) {
            int userNo = applicants.get(i);
            data.put("userNo", userNo);
            session.insert("chatMapper.insertChatMember", data);
        }

    }
}
