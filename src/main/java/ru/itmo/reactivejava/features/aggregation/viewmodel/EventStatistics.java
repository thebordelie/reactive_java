package ru.itmo.reactivejava.features.aggregation.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class EventStatistics {
    private long totalEvents;
    private long totalMembers;
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
        sb.append("Среднее количество участников на событиях: ").append(avgMembersPerEvent).append("\n");
        sb.append("Максимальная вместимость площадок: ").append(maxCapacity).append("\n");
        sb.append("Минимальная вместимость площадок: ").append(minCapacity).append("\n");
        sb.append("Средняя вместимость площадок: ").append(avgCapacity);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStatistics that = (EventStatistics) o;
        if (totalEvents != that.totalEvents) return false;
        if (totalMembers != that.totalMembers) return false;
        if (Double.compare(that.avgMembersPerEvent, avgMembersPerEvent) != 0) return false;
        if (maxCapacity != that.maxCapacity) return false;
        if (minCapacity != that.minCapacity) return false;
        if (Double.compare(that.avgCapacity, avgCapacity) != 0) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(totalEvents);
        result = 31 * result + Long.hashCode(totalMembers);
        result = 31 * result + Double.hashCode(avgMembersPerEvent);
        result = 31 * result + Integer.hashCode(maxCapacity);
        result = 31 * result + Integer.hashCode(minCapacity);
        result = 31 * result + Double.hashCode(avgCapacity);
        return result;
    }

}
