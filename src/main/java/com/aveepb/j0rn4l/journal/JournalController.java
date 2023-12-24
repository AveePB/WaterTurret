package com.aveepb.j0rn4l.journal;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    @GetMapping("/read")
    public ResponseEntity<String> read() {
        return ResponseEntity.ok("Congrats mate, it's ur journal!!");
    }
}
