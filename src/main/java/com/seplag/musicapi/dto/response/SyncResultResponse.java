package com.seplag.musicapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResultResponse {

    private int inserted;
    private int inactivated;
    private int updated;
    private String message;
}
