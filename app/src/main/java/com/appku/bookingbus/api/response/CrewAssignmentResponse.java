package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.CrewAssignment;
import java.util.List;

public class CrewAssignmentResponse {
    private boolean success;
    private List<CrewAssignment> data;

    public boolean isSuccess() { return success; }
    public List<CrewAssignment> getData() { return data; }
}
