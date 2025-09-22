package ru.itmo.reactivejava.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EventStatistics {
    private long totalEvents;
    private long totalMembers;
    private long totalUsers;
    private double avgMembersPerEvent;
    private int maxCapacity;
    private int minCapacity;
    private double avgCapacity;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Статистика по музыкальных соревнованиям.\n");
        sb.append("Количество музыкальных соревнований: ").append(totalEvents).append("\n");
        sb.append("Количество участников: ").append(totalMembers).append("\n");
        sb.append("Количество зрителей: ").append(totalUsers).append("\n");
        sb.append("Среднее количество участников на событиях: ").append(avgMembersPerEvent).append("\n");
        sb.append("Максимальная вместимость площадок: ").append(maxCapacity).append("\n");
        sb.append("Минимальная вместимость площадок: ").append(minCapacity).append("\n");
        sb.append("Средняя вместимость площадок: ").append(avgCapacity);
        return sb.toString();
    }
}
