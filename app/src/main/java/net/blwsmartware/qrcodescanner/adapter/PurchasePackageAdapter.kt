package net.blwsmartware.qrcodescanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.google.android.material.button.MaterialButton
import io.sad.monster.dialog.AppPurchase
import net.blwsmartware.qrcodescanner.R

class PurchasePackageAdapter(
    private val packages: List<ProductDetails>,
    private val onPackageSelected: (ProductDetails) -> Unit
) : RecyclerView.Adapter<PurchasePackageAdapter.PackageViewHolder>() {

    class PackageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageName: TextView = view.findViewById(R.id.packageNameTextView)
        val price: TextView = view.findViewById(R.id.priceTextView)
        val buyButton: MaterialButton = view.findViewById(R.id.buyButton)
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
        holder.buyButton.setOnClickListener {
            onPackageSelected(packageItem)
        }
    }

    override fun getItemCount() = packages.size
}