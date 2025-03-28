package io.sad.monster.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sad.monster.callback.PurchaseListener;
import io.sad.monster.util.SharePreferenceUtils;

public class AppPurchase {
    public static boolean REMOVE_IAP_VERSION = false;
    public boolean IAP_BUY_REPEAT = false;
    public static final String IAP_BARCODE = "qr_barcode";
    public static final String IAP_PHONE_CREATE_QR = "phone_create_qr";
    public static final String IAP_LOCATION = "localtion_create_qr";
    public static final String IAP_URL_CREATE = "url_create_qr";
    public static final String IAP_EMAIL_CREATE = "email_create_qr";
    public static final String IAP_MESSAGE_CREATE = "message_create_qr";

    private static final String TAG = "PurchaseEG";
    @SuppressLint("StaticFieldLeak")
    private static AppPurchase instance;
    final private Map<String, ProductDetails> skuDetailsINAPMap = new HashMap<>();
    private ArrayList<String> listINAPId;
    public ArrayList<String> listIdReStore;
    private PurchaseListener purchaseListener;
    private Boolean isInitBillingFinish = false;
    private BillingClient billingClient;
    public ArrayList<ProductDetails> skuListINAPFromStore = new ArrayList<>();

    private final Context mContext;

    private boolean verified = false;
    private boolean verifiedINAP = false;
    private boolean verifiedSUBS = false;
    private boolean isPurchase = false;
    private Purchase latestPurchase;

    PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> list) {
            Log.e("VuLT", "onPurchasesUpdated code: " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase : list) {
                    latestPurchase = purchase;
                    if (IAP_BUY_REPEAT) {
                        consumePurchaseIAP(purchase);
                        Log.d("VuLT", "IAP_BUY_REPEAT:1 ");
                    } else {
                        Log.d("VuLT", "IAP_BUY_REPEAT: 0");
                        handlePurchase(purchase);
                        acknowledgePurchase(purchase);
                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                if (purchaseListener != null)
                    purchaseListener.onUserCancelBilling();
                Log.d("VuLT", "onPurchasesUpdated:USER_CANCELED ");
            } else {
                Log.d("VuLT", "onPurchasesUpdated:... ");
            }
        }
    };

    public void setUpIsPurchase(Context context) {
        setIsPurchased(SharePreferenceUtils.getIsPurchase(context));
    }

    public void initBilling(Application application) {
        setIsPurchased(SharePreferenceUtils.getIsPurchase(application));
        this.listINAPId = new ArrayList<>();
        this.listINAPId.add(IAP_BARCODE);
        this.listINAPId.add(IAP_PHONE_CREATE_QR);
        this.listINAPId.add(IAP_LOCATION);
        this.listINAPId.add(IAP_URL_CREATE);
        this.listINAPId.add(IAP_EMAIL_CREATE);
        this.listINAPId.add(IAP_MESSAGE_CREATE);

        billingClient = BillingClient.newBuilder(application)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (!isInitBillingFinish) {
                    verifyPurchased();
                }
                isInitBillingFinish = true;
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryINAPP();
                }
            }
        });
    }

    public void restorePurchases(Runnable runnable) {

        billingClient = BillingClient.newBuilder(mContext).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d("VuLT", "onPurchasesUpdated:... 5");

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult1, list) -> {
                                if (!list.isEmpty()) {
                                    verifyPurchased(runnable);
                                } else {
                                    runnable.run(); // Gọi callback nếu không có gói nào đã mua
                                }
                            });
                } else {
                    runnable.run();
                }
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        if (purchaseListener != null) {
            purchaseListener.onProductPurchased(purchase.getOrderId(), purchase.getOriginalJson());
        }

        if (REMOVE_IAP_VERSION) {
            consumePurchase(purchase);
        } else {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                isPurchase = true;
                if (mContext != null) {
                    SharePreferenceUtils.putIsPurchase(mContext, true);
                }
            }
        }
    }

    private void queryINAPP() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        for (String sku : listINAPId) {
            productList.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(sku)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            );
        }
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
        billingClient.queryProductDetailsAsync(params, (billingResult, list) -> {
            skuListINAPFromStore.clear();
            skuListINAPFromStore.addAll(list);
            addSkuINAPPToMap(list);

        });

    }

    private void addSkuINAPPToMap(List<ProductDetails> skuList) {
        for (ProductDetails skuDetails : skuList) {
            skuDetailsINAPMap.put(skuDetails.getProductId(), skuDetails);
        }
    }

    private AppPurchase(Context context) {
        this.mContext = context;
    }

    public static AppPurchase getInstance(Context context) {
        if (instance == null) {
            instance = new AppPurchase(context);
        }
        return instance;
    }

    public void setPurchaseListener(PurchaseListener purchaseListener) {
        this.purchaseListener = purchaseListener;
    }


    public boolean isPurchased() {
        return isPurchase;
    }

    public void setIsPurchased(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public boolean isPurchased(Context context) {
        return isPurchase;
    }

    public void verifyPurchased() {
        verified = false;

        if (listINAPId != null && !listINAPId.isEmpty()) {
            billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(), (billingResult, list) -> {
                if (mContext != null) {
                    list.forEach(this::acknowledgePurchase);
                    ((Activity) mContext).runOnUiThread(() -> {
                        try {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (!verifiedSUBS || !verified) {
                                    SharePreferenceUtils.putIsPurchase(mContext, false);
                                    isPurchase = false;
                                }

                                if (REMOVE_IAP_VERSION) {
                                    for (Purchase purchase : list) {
                                        consumePurchase(purchase);
                                    }
                                } else {
                                    for (Purchase purchase : list) {
                                        for (String id : listINAPId) {
                                            if (purchase.getProducts().contains(id)) {
                                                isPurchase = true;
                                                SharePreferenceUtils.putIsPurchase(mContext, true);
                                                if (!verified) {
                                                    verified = true;
                                                    verifiedINAP = true;
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            verifiedINAP = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    public void verifyPurchased(Runnable runnable) {
        verified = false;
        if (listINAPId != null && !listINAPId.isEmpty()) {

            billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(), (billingResult, list) -> {
                if (mContext != null) {

                    list.forEach(this::acknowledgePurchase);
                    ((Activity) mContext).runOnUiThread(() -> {
                        try {

                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                if (!verifiedSUBS || !verified) {
                                    SharePreferenceUtils.putIsPurchase(mContext, false);
                                    isPurchase = false;
                                }

                                if (REMOVE_IAP_VERSION) {
                                    for (Purchase purchase : list) {
                                        consumePurchase(purchase);
                                    }
                                } else {
                                    for (Purchase purchase : list) {
                                        for (String id : listINAPId) {
                                            if (purchase.getProducts().contains(id)) {
                                                isPurchase = true;
                                                listIdReStore.add(id);
                                                SharePreferenceUtils.putIsPurchase(mContext, true);
                                                runnable.run();
                                                if (!verified) {
                                                    verified = true;
                                                    verifiedINAP = true;
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            verifiedINAP = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    public void purchase(Activity activity, ProductDetails productDetails) {
        if (activity == null || productDetails == null) return;
        purchaseInApp(activity, productDetails);
    }

    public String purchaseInApp(Activity activity, ProductDetails productDetails) {
        if (skuListINAPFromStore == null) {
            if (purchaseListener != null)
                purchaseListener.displayErrorMessage("Billing error init");
            return "";
        }

        if (productDetails == null) {
            return "Product ID invalid";
        }

        String offerToken;
        try {
            if (productDetails.getSubscriptionOfferDetails() != null) {
                for (int i = 0; i < productDetails.getSubscriptionOfferDetails().size(); i++) {
                    offerToken = productDetails.getSubscriptionOfferDetails().get(i).getOfferToken();
                    if (!offerToken.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                List.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build());


        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        BillingResult responseCode = billingClient.launchBillingFlow(activity, billingFlowParams);
        if (responseCode.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            if (latestPurchase != null) {
                consumePurchaseIAP(latestPurchase); // Tiêu thụ sản phẩm để có thể mua lại
            }
            return "Subscribed Successfully";
            //}
        }
        return "";
    }

    public String subscribe(Activity activity, ProductDetails productDetails) {
        if (productDetails == null) {
            return "SubsId invalid";
        }
        String offerToken = "";
        if (productDetails.getSubscriptionOfferDetails() != null) {
            for (int i = 0; i < productDetails.getSubscriptionOfferDetails().size(); i++) {
                offerToken = productDetails.getSubscriptionOfferDetails().get(i).getOfferToken();
                if (!offerToken.isEmpty()) {
                    break;
                }
            }
        }
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                List.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build());


        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        BillingResult responseCode = billingClient.launchBillingFlow(activity, billingFlowParams);
        switch (responseCode.getResponseCode()) {
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Billing not supported for type of request");
                return "Billing not supported for type of request";
            }
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
                 BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                return "";
            }
            case BillingClient.BillingResponseCode.ERROR -> {
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Error completing request");
                return "Error completing request";
            }
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                return "Error processing request.";
            }
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                return "Selected item is already owned";
            }
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                return "Item not available";
            }
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                return "Play Store service is not connected now";
            }
            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> {
                return "Timeout";
            }
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Network error.");
                return "Network Connection down";
            }
            case BillingClient.BillingResponseCode.USER_CANCELED -> {
                if (purchaseListener != null)
                    purchaseListener.displayErrorMessage("Request Canceled");
                return "Request Canceled";
            }
            case BillingClient.BillingResponseCode.OK -> {
                return "Subscribed Successfully";
            }
            //}
        }

        return "";
    }

    public void consumePurchaseIAP(Purchase pc) {
        if (pc == null)
            return;
        try {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(pc.getPurchaseToken())
                    .build();

            ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e("VuLT", "onConsumeResponse: OK ->" + purchaseListener);

                    if (purchaseListener != null) {
                        purchaseListener.onUserPurchaseConsumable();
                    }
                }
            };

            billingClient.consumeAsync(consumeParams, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void consumePurchase(Purchase pc) {
        if (pc == null)
            return;
        try {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(pc.getPurchaseToken())
                    .build();

            ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "onConsumeResponse: OK");
                    verifyPurchased();
                }
            };

            billingClient.consumeAsync(consumeParams, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPrice(ProductDetails productDetails) {
        if (productDetails == null) return "";
        try {
            if (productDetails.getProductType().equals("subs")) {
                return getPriceSub(productDetails);
            } else {
                return getPriceInApp(productDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPriceInApp(ProductDetails productDetails) {
        if (productDetails == null)
            return "";
        if (productDetails.getOneTimePurchaseOfferDetails() != null) {
            return productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
        }
        return "";
    }

    public static String getPriceSub(ProductDetails productDetails) {
        if (productDetails == null) return "";
        if (productDetails.getSubscriptionOfferDetails() == null) return "";
        try {
            return productDetails.getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private String formatCurrency(double price, String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance(currency));
        return format.format(price);
    }

    private void acknowledgePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> Log.e("VuLT", "acknowledgePurchase: " + billingResult.getResponseCode()));
            }
        }
    }

    @IntDef({TYPE_IAP.PURCHASE, TYPE_IAP.SUBSCRIPTION})
    public @interface TYPE_IAP {
        int PURCHASE = 1;
        int SUBSCRIPTION = 2;
    }
}


