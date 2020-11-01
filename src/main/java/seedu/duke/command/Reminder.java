package seedu.duke.command;

import seedu.duke.data.Budget;
import seedu.duke.data.RepaymentList;
import seedu.duke.data.SpendingList;
import seedu.duke.ui.Ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Reminder extends Command {
    private LocalDate startWeek;
    private WarnCommand warn;
    private ArrayList<String> week = new ArrayList<>();

    public Reminder() {
        saveDatesToList();
        warn = new WarnCommand();
    }

    @Override
    public void execute(SpendingList spendingList, RepaymentList repaymentList, Ui ui) {
        double amountRemained = 0;
        if (Budget.hasBudget) {
            warn.execute(spendingList, null, ui);
            amountRemained = findRemainingAmount(spendingList);
        }

        double totalAmountSpent = 0;
        for (String i: week) {
            totalAmountSpent += spendingList.getSpendingAmountTime(i);
        }

        ui.printReminderMessage(totalAmountSpent, amountRemained, startWeek.toString());
    }

    private LocalDate startOfWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek day = today.getDayOfWeek();
        switch (day) {
        case MONDAY:
            startWeek = today;
            break;
        case TUESDAY:
            startWeek = today.minusDays(1);
            break;
        case WEDNESDAY:
            startWeek = today.minusDays(2);
            break;
        case THURSDAY:
            startWeek = today.minusDays(3);
            break;
        case FRIDAY:
            startWeek = today.minusDays(4);
            break;
        case SATURDAY:
            startWeek = today.minusDays(5);
            break;
        case SUNDAY:
            startWeek = today.minusDays(6);
            break;
        default:
            break;
        }

        return startWeek;
    }

    private void saveDatesToList() {
        LocalDate startDay = startOfWeek();
        for (int i = 0; i < 7; i++) {
            week.add(startDay.minusDays(-i).toString());
        }
    }

    public double findRemainingAmount(SpendingList spendingList) {
        double budgetLimit = Budget.getBudgetLimit();
        double currentAmount = spendingList.getCurrentAmount();
        return budgetLimit - currentAmount;
    }
}