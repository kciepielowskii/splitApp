package com.splitapp.splitApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String ownerName;
    private List<String> memberNames;
    private LocalDateTime createdAt;
}