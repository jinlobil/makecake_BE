package com.project.makecake.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 400
    POST_VALUE_NULL(HttpStatus.BAD_REQUEST, "게시글에 빈값이 있습니다."),
    COMMENT_CONTENT_NULL(HttpStatus.BAD_REQUEST, "댓글에 content가 없습니다."),
    REVIEW_CONTENT_NULL(HttpStatus.BAD_REQUEST, "리뷰에 content가 없습니다."),
    ORDER_CONTENT_NULL(HttpStatus.BAD_REQUEST, "주문서가 다 채워지지 않았습니다."),
    NOTI_MAIN_CONTENT_NULL(HttpStatus.BAD_REQUEST, "알림에 maincontent가 없습니다."),
    NOTI_NICKNAME_NULL(HttpStatus.BAD_REQUEST, "알림에 nickname이 없습니다."),
    TITLE_LENGTH_WRONG(HttpStatus.BAD_REQUEST, "title의 글자수 제한을 넘었습니다."),
    CONTENT_LENGTH_WRONG(HttpStatus.BAD_REQUEST, "content의 글자수 제한을 넘었습니다."),
    NOT_IMAGEFILE(HttpStatus.BAD_REQUEST, "이미지 파일이 아닙니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "패스워드가 일치하지 않습니다."),
    BAD_NOTITYPE(HttpStatus.BAD_REQUEST, "잘못된 알림 타입입니다."),
    NOT_ADD_NOTITYPE(HttpStatus.BAD_REQUEST, "추가할 수 없는 알림 타입입니다."),
    NOT_FIX_NOTITYPE(HttpStatus.BAD_REQUEST, "고정할 수 없는 알림 타입입니다."),
    NOT_SEND_NOTITYPE(HttpStatus.BAD_REQUEST, "발송할 수 없는 알림 타입입니다."),
    // 401
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패!"),
    EMAIL_PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, "아이디/패스워드가 틀립니다."),
    UNAUTHORIZED_MAMBER(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    // 403
    NOT_POST_OWNER(HttpStatus.FORBIDDEN, "로그인한 사용자의 게시물이 아닙니다."),
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "로그인한 사용자의 댓글이 아닙니다."),
    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "로그인한 사용자의 리뷰가 아닙니다."),
    NOT_DESIGN_OWNER(HttpStatus.FORBIDDEN, "로그인한 사용자의 도안이 아닙니다."),
    NOT_ORDER_OWNER(HttpStatus.FORBIDDEN, "로그인한 사용자의 주문이 아닙니다."),
    // 404
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
    CAKE_NOT_FOUND(HttpStatus.NOT_FOUND, "케이크가 존재하지 않습니다."),
    DESIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "도안이 존재하지 않습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "스토어가 존재하지 않습니다."),
    ORDERFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문서양식이 존재하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    NOTI_NOT_FOUND(HttpStatus.NOT_FOUND, "알림이 존재하지 않습니다."),
    FIXNOTI_NOT_FOUND(HttpStatus.NOT_FOUND, "고정알림이 존재하지 않습니다."),
    // 409
    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "중복된 이메일이 존재합니다."),
    NICKNAME_DUPLICATE(HttpStatus.CONFLICT, "중복된 닉네임이 존재합니다."),
    LIKE_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_EXIST(HttpStatus.CONFLICT, "이미 좋아요 취소를 눌렀습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
