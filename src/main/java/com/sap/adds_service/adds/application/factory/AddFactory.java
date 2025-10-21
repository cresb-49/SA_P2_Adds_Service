package com.sap.adds_service.adds.application.factory;

import com.sap.adds_service.adds.application.output.FindCinemaPort;
import com.sap.adds_service.adds.application.output.FindUserPort;
import com.sap.adds_service.adds.domain.Add;
import com.sap.adds_service.adds.domain.dtos.CinemaView;
import com.sap.adds_service.adds.domain.dtos.UserView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AddFactory {
    private final FindCinemaPort findCinemaPort;
    private final FindUserPort findUserPort;

    public Add withCinemaAndUser(Add add) {
        var cinema = findCinemaPort.findById(add.getCinemaId());
        var user = findUserPort.findById(add.getUserId());
        add.setCinema(cinema);
        add.setUser(user);
        return add;
    }

    public List<Add> withCinemaAndUser(List<Add> adds) {
        // Consult and map cinemas and users in batch for optimization
        var cinemaIds = adds.stream().map(Add::getCinemaId).distinct().toList();
        var userIds = adds.stream().map(Add::getUserId).distinct().toList();
        var cinemas = findCinemaPort.findByIds(cinemaIds);
        var users = findUserPort.findByIds(userIds);
        var cinemaMap = cinemas.stream().collect(java.util.stream.Collectors.toMap(CinemaView::id, c -> c));
        var userMap = users.stream().collect(java.util.stream.Collectors.toMap(UserView::id, u -> u));
        for (var add : adds) {
            add.setCinema(cinemaMap.get(add.getCinemaId()));
            add.setUser(userMap.get(add.getUserId()));
        }
        return adds;
    }
}
