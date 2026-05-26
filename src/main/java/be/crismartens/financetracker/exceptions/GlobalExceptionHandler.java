package be.crismartens.financetracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountInfoNotFoundException.class)
    public ResponseEntity<AppError> handleAccountInfoNotFoundException(AccountInfoNotFoundException ex) {
        AppError error = new AppError(
                Instant.now(),
                404,
                "User Info not found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryBudgetNotFoundException.class)
    public ProblemDetail handleCategoryBudgetNotFoundException(CategoryBudgetNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Category Budget Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFoundException(CategoryNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Category Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(EmptyExpenseException.class)
    public ProblemDetail handleEmptyExpenseException(EmptyExpenseException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Empty Expense");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ProblemDetail handleExpenseNotFoundException(ExpenseNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Expense Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(InvalidExpenseException.class)
    public ProblemDetail handleInvalidExpenseException(InvalidExpenseException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Invalid Expense");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(InvalidUserException.class)
    public ProblemDetail handleInvalidUserException(InvalidUserException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Invalid User");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(NoIncomeAddedException.class)
    public ProblemDetail handleNoIncomeAddedException(NoIncomeAddedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("No Income Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(NullValueException.class)
    public ProblemDetail handleNullValueException(NullValueException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Null Value");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(UnauthorisedAccessException.class)
    public ProblemDetail handleUnauthorisedAccessException(UnauthorisedAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problem.setTitle("Unauthorised Access");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(UsernameAlreadyInUseExcepion.class)
    public ProblemDetail handleUsernameAlreadyInUseException(UsernameAlreadyInUseExcepion e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Username Already In Use");
        problem.setDetail(e.getMessage());
        return problem;
    }

    record AppError(Instant timestamp, int status, String error, String message) {}
}
