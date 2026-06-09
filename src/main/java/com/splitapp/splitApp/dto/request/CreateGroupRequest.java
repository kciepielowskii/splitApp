package com.splitapp.splitApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {

    @NotBlank
    private String name;

    private List<Long> memberIds;
}