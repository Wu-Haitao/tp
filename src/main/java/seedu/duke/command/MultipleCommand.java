package seedu.duke.command;

import seedu.duke.data.RepaymentList;
import seedu.duke.data.SpendingList;
import seedu.duke.exceptions.InvalidClearRepaymentException;
import seedu.duke.exceptions.InvalidClearSpendingException;
import seedu.duke.exceptions.InvalidMonthException;
import seedu.duke.exceptions.InvalidIndexException;
import seedu.duke.exceptions.InvalidClearBudgetException;
import seedu.duke.ui.Ui;

import java.io.IOException;
import java.util.ArrayList;

public class MultipleCommand extends Command {
    public ArrayList<Command> commands = new ArrayList<>();

    public void addCommand(Command c) {
        commands.add(c);
    }

    public boolean noCommandsGiven() {
        return commands.isEmpty();
    }

    @Override
    public void execute(SpendingList spendingList, RepaymentList repaymentList, Ui ui) throws IOException,
            InvalidMonthException, InvalidIndexException, InvalidClearRepaymentException, InvalidClearSpendingException,
            InvalidClearBudgetException {
        for (Command c : commands) {
            c.execute(spendingList, repaymentList, ui);
        }
    }
}
