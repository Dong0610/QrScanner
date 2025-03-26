package io.sad.monster.callback;

public interface PurchaseListener {
    void onProductPurchased(String productId, String transactionDetails);
    void displayErrorMessage(String errorMsg );
    void onUserCancelBilling();
    void onUserPurchaseConsumable();
}
