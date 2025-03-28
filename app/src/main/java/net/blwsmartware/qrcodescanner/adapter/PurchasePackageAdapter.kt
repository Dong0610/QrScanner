package net.blwsmartware.qrcodescanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.google.android.material.button.MaterialButton
import io.sad.monster.dialog.AppPurchase
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.isCreateBarcode
import net.blwsmartware.qrcodescanner.app.isEmailCreateQr
import net.blwsmartware.qrcodescanner.app.isLocationCreateQr
import net.blwsmartware.qrcodescanner.app.isMessageCreateQr
import net.blwsmartware.qrcodescanner.app.isPhoneCreateQr
import net.blwsmartware.qrcodescanner.app.isUrlCreateQr

class PurchasePackageAdapter(
    private val packages: List<ProductDetails>,
    private val onPackageSelected: (ProductDetails) -> Unit
) : RecyclerView.Adapter<PurchasePackageAdapter.PackageViewHolder>() {

    class PackageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageName: TextView = view.findViewById(R.id.packageNameTextView)
        val price: TextView = view.findViewById(R.id.priceTextView)
        val buyButton: MaterialButton = view.findViewById(R.id.buyButton)
        val ivDone: ImageView = view.findViewById(R.id.ivDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase_package, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packages[position]
        holder.packageName.text = packageItem.name
        holder.price.text = AppPurchase.getPriceInApp(packageItem)
        holder.buyButton.isVisible = !setState(packageItem.productId)
        holder.ivDone.isVisible = setState(packageItem.productId)
        holder.buyButton.setOnClickListener {
            onPackageSelected(packageItem)
        }
    }


    private fun setState(productId: String):Boolean {
        return when (productId) {
            "email_create_qr" -> {
                isEmailCreateQr
            }

            "localtion_create_qr" -> {
                isLocationCreateQr
            }

            "message_create_qr" -> {
                isMessageCreateQr
            }

            "phone_create_qr" -> {
                isPhoneCreateQr
            }

            "qr_create_barcode" -> {
                isCreateBarcode
            }

            "url_create_qr" -> {
                isUrlCreateQr
            }

            else -> {
                isEmailCreateQr
            }
        }
    }

    override fun getItemCount() = packages.size
}