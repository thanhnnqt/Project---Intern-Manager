package org.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private long totalInterns;
    private long totalMentors;
    private long totalTasks;
    private long completedTasks;
    private List<InternResponse> recentInterns;
    private List<TaskResponse> recentTasks;
}
