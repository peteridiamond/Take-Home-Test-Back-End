package com.example.demo.hook;

import com.example.demo.exception.DuplicateCallbackException;
import com.example.demo.exception.TimePeriodException;
import com.example.demo.model.Change;
import com.example.demo.model.Deregister;
import com.example.demo.model.Register;
import com.example.demo.model.ServiceTime;
import com.example.demo.service.CallbackService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
public class WebHookController {

    final
    CallbackService callbackService;

    public WebHookController(CallbackService callbackService) {
        this.callbackService = callbackService;
    }

    /**
     * Stub for callback
     *
     * @param serviceTime
     */
    @PostMapping(value = "/callback")
    void callback(@NonNull @RequestBody ServiceTime serviceTime) {
        log.info("Callback listener received [" + serviceTime + "]");
    }

    /**
     * - **Register:** provides
     * (a) a URL to which the time should be sent and
     * (b) the frequency at which callbacks should be sent.
     * Should error if the client URL is already registered for callbacks.
     * Once called, the callbacks should begin.
     *
     * @param register
     */
    @PostMapping(value = "/webhook", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    void register(@NonNull @RequestBody Register register) throws DuplicateCallbackException, TimePeriodException {
        callbackService.register(register);
    }

    /**
     * Deregister: provides
     * a URL previously registered.
     * Should error if the client URL is not registered for callbacks.
     * Once called, callbacks to that URL should stop.
     *
     * @param deregister
     */
    @DeleteMapping(value = "/webhook", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    void unRegister(@NonNull @RequestBody Deregister deregister) {
        callbackService.deRegister(deregister);
    }

    /**
     * Change the frequency of callbacks for
     * a specific URL, to any time between 5 seconds and 4 hours.
     *
     * @param change
     */
    @PutMapping(value = "/webhook", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    void change(@NonNull @RequestBody Change change) throws DuplicateCallbackException, TimePeriodException {
        callbackService.change(change);
    }

    //TODO: Move to Advice
    @ExceptionHandler(DuplicateCallbackException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateCallbackException(DuplicateCallbackException ce) {
        return ce.getMessage();
    }

    @ExceptionHandler(TimePeriodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTimePeriodException(TimePeriodException tpe) {
        return tpe.getMessage();
    }
}
