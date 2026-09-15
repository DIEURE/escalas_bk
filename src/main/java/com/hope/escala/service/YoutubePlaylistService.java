package com.hope.escala.service;

 
import org.springframework.stereotype.Service;

@Service
public class YoutubePlaylistService {

    public String gerarLinkPlaylistFake(Long escalaId) {

        return "https://youtube.com/playlist?list=ESCALA_"
                + escalaId;
    }
}