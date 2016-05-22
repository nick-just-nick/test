package ru.mnv.rvlt.core.model;

@FunctionalInterface
interface RequestCallback {
    Result execute(DailyBalance balance);
}
