package ru.job4j.github.analysis.comparator;

import org.springframework.stereotype.Component;
import ru.job4j.github.analysis.entity.CommitEntity;

import java.util.Comparator;

@Component
public class CommitDateComparator implements Comparator<CommitEntity> {
    @Override
    public int compare(CommitEntity left, CommitEntity right) {
        return left.getAuthorDate().compareTo(right.getAuthorDate());
    }
}
