package com.slamdunk.WORK.service;

import com.slamdunk.WORK.dto.request.ObjectiveRequest;
import com.slamdunk.WORK.dto.response.ObjectiveResponse;
import com.slamdunk.WORK.entity.Objective;
import com.slamdunk.WORK.repository.ObjectiveRepository;
import com.slamdunk.WORK.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ObjectiveService {
    private final ObjectiveRepository objectiveRepository;
    private final UserObjectiveService userObjectiveService;

    //목표 생성
    @Transactional
    public ResponseEntity<?> registerObjective(ObjectiveRequest objectiveRequest, UserDetailsImpl userDetails) {
        Optional<Objective> objectiveCheck = objectiveRepository.findByObjective(objectiveRequest.getObjective());
        if (objectiveCheck.isPresent()) {
            return new ResponseEntity<>("오브젝트 이름이 중복됩니다.", HttpStatus.BAD_REQUEST);
        } else {
            Objective newObjective = new Objective(objectiveRequest);
            objectiveRepository.save(newObjective);

            userObjectiveService.registerUserObjective(newObjective, userDetails);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    //목표 전체 조회
    public ResponseEntity<?> allObjective(UserDetailsImpl userDetails) {
        List<Long> objectiveId = userObjectiveService.allObjective(userDetails);

        List<ObjectiveResponse> objectiveResponseList = new ArrayList<>();
        for (int i=0; i<objectiveId.size(); i++) {
            Optional<Objective> objective = objectiveRepository.findById(objectiveId.get(i));
            if (objective.isPresent()) {
                ObjectiveResponse objectiveResponse = new ObjectiveResponse(
                        objective.get().getId(),
                        objective.get().getObjective(),
                        objective.get().getStartDate(),
                        objective.get().getEndDate(),
                        objective.get().getColor(),
                        objective.get().getProgress()
                );
                objectiveResponseList.add(objectiveResponse);
            }
        }

        return new ResponseEntity<>(objectiveResponseList, HttpStatus.OK);
    }
}
