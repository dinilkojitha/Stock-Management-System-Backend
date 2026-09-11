/**
 * Service class handling core business logic for stock request creation, allocation, and issuing.
 */
package com.example.stockmanagementsystembackend.domain.distribution.service;

import com.example.stockmanagementsystembackend.domain.distribution.repository.InternalRequestRepository;
import org.springframework.stereotype.Service;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequest;
import java.util.List;

@Service
public class InternalRequestService {

    private final InternalRequestRepository internalRequestRepository;

    public InternalRequestService(InternalRequestRepository internalRequestRepository) {
        this.internalRequestRepository=internalRequestRepository;
    }

    public List<InternalRequest> getAllRequest(){
        return internalRequestRepository.findAll();
    }

    public InternalRequest createRequest(InternalRequest request){
        return internalRequestRepository.save(request);
    }

    public InternalRequest updateRequest(Integer id, InternalRequest request){
        InternalRequest existingRequest=
                internalRequestRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Request not found"));

        existingRequest.setDepartmentDepartment(request.getDepartmentDepartment());
        existingRequest.setUserUserid(request.getUserUserid());
        existingRequest.setRequestTime(request.getRequestTime());
        existingRequest.setStatus(request.getStatus());

        return internalRequestRepository.save(existingRequest);

    }

    public void deleteRequest(Integer id){
        internalRequestRepository.deleteById(id);
    }


}
