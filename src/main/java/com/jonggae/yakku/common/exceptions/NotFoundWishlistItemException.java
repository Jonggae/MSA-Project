package com.jonggae.yakku.common.exceptions;

public class NotFoundWishlistItemException extends RuntimeException{
    public NotFoundWishlistItemException() {
        super("위시리스트에 해당 상품이 없습니다.");
    }
}
