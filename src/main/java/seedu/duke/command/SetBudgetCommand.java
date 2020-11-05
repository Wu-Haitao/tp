package seedu.duke.command;

import seedu.duke.data.Budget;
import seedu.duke.data.RepaymentList;
import seedu.duke.data.SpendingList;
import seedu.duke.exceptions.InvalidBudgetException;
import seedu.duke.exceptions.InvalidInputCurrencyException;
import seedu.duke.ui.Ui;
import seedu.duke.utilities.DecimalFormatter;

//@@author killingbear999
public class SetBudgetCommand extends Command {

    private double budgetLimit;
    private String currency;

    
    public SetBudgetCommand(String currency, Double budgetLimit) {
        this.currency = currency;
        this.budgetLimit = budgetLimit;
    }

    @Override
    public void execute(SpendingList spendingList, RepaymentList repaymentList, Ui ui)
            throws InvalidInputCurrencyException, InvalidBudgetException {
        if (budgetLimit < 0.01) {
            throw new InvalidBudgetException();
        }
        if (!(currency.equals("SGD") || currency.equals("USD") || currency.equals("CNY"))) {
            throw new InvalidInputCurrencyException();
        }
        DecimalFormatter decimalFormatter = new DecimalFormatter();
        budgetLimit = decimalFormatter.convert(budgetLimit);
        Budget.addBudget(currency, budgetLimit);
        ui.printBudgetLimit(currency, budgetLimit);
    }
}
