package com.appchemist.distribute_pay.web;

import com.appchemist.distribute_pay.application.port.in.DistributePayUseCase;
import com.appchemist.distribute_pay.application.port.in.InquiryDistributePayUseCase;
import com.appchemist.distribute_pay.application.port.in.PickUpUseCase;
import com.appchemist.distribute_pay.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@WebAdapter
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/{version:v[\\d]+}/distributepay", produces = MediaType.APPLICATION_JSON_VALUE)
class DistributePayController {
    private final String USER_ID_HEADER_NAME = "X-USER-ID";
    private final String ROOM_ID_HEADER_NAME = "X-ROOM-ID";

    private final DistributePayUseCase distributePayUseCase;
    private final PickUpUseCase pickUpUseCase;
    private final InquiryDistributePayUseCase inquiryDistributePayUseCase;

    @PostMapping
    @ResponseBody
    public Mono<ResponseEntity<?>> createDistributePay(
            @RequestHeader(USER_ID_HEADER_NAME) @Min(1) long userId,
            @RequestHeader(ROOM_ID_HEADER_NAME) @NotEmpty String roomId,
            @PathVariable("version") String version,
            @ModelAttribute @Valid Mono<DistributePayReqeust> distributePayReqeust
    ) {
        if (version.equalsIgnoreCase("v1")) {
            return distributePayReqeust
                    .flatMap(request -> distributePayUseCase.distributePay(userId, roomId, request.getMaxPay(), request.getMaxTarget()))
                    .map(o -> {
                        if (o == null) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal Server Error"));
                        }
                        return ResponseEntity.ok(new CreateResponse(o.getToken()));
                    })
                    .onErrorResume(RuntimeException.class
                            , t -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(t.getMessage()))));
        }
        return Mono.just(ResponseEntity.notFound().build());
    }

    @PutMapping("/{token:[a-zA-Z]{3}}")
    @ResponseBody
    public Mono<ResponseEntity<?>> pickUpPay(
            @RequestHeader(USER_ID_HEADER_NAME) @Min(1) long userId,
            @RequestHeader(ROOM_ID_HEADER_NAME) @NotEmpty String roomId,
            @PathVariable("version") String version,
            @PathVariable("token") String token
    ) {
        if (version.equalsIgnoreCase("v1")) {
            return pickUpUseCase.pickUpPay(token, userId, roomId)
                    .map(o -> {
                        if (o == null) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal Server Error"));
                        }
                        return ResponseEntity.ok(new PickUpResponse(o.getAmount()));
                    })
                    .onErrorResume(RuntimeException.class
                            , t -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(t.getMessage()))));
        }
        return Mono.just(ResponseEntity.notFound().build());
    }

    @GetMapping("/{token:[a-zA-Z]{3}}")
    @ResponseBody
    public Mono<ResponseEntity<?>> getDistributePay(
            @RequestHeader(USER_ID_HEADER_NAME) @Min(1) long userId,
            @RequestHeader(ROOM_ID_HEADER_NAME) @NotEmpty String roomId,
            @PathVariable("version") String version,
            @PathVariable("token") String token
    ) {
        if (version.equalsIgnoreCase("v1")) {
            return inquiryDistributePayUseCase.inquiry(token, userId, roomId)
                    .map(o -> {
                        if (o == null) {
                            return ResponseEntity.ok("Not Exist Token");
                        }
                        return ResponseEntity.ok(o);
                    })
                    .onErrorResume(RuntimeException.class
                            , t -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(t.getMessage()))));
        }
        return Mono.just(ResponseEntity.notFound().build());
    }
}
