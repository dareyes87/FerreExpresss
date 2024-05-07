package com.example.ferreexpress.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.SliderAdapter
import com.example.ferreexpress.Domain.SliderItems
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.Fragment.DescriptionFragment
import com.example.ferreexpress.Fragment.ReviewFragment
import com.example.ferreexpress.Fragment.SoldFragment
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.databinding.ActivityDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: itemsDomain
    private var numberOrder: Int = 1
    private lateinit var managmentCart: ManagmentCart
    private val slideHandler: Handler = Handler()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cosas ocultas por default
        binding.deleteBtn.visibility = View.GONE
        binding.editBtn.visibility = View.GONE

        var key = intent.getStringExtra("keyProduct")
        val isSeller = intent.getBooleanExtra("isSeller", false)
        if (isSeller) {
            // Si es vendedor, ocultar lo siguiente
            binding.addTocartBtn.visibility = View.GONE
            binding.favBtn.visibility = View.GONE

            //Si es vendedor, agregar lo siguiente
            binding.deleteBtn.visibility = View.VISIBLE
            binding.editBtn.visibility = View.VISIBLE
        }

        binding.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar")
            builder.setMessage("¿Estás seguro de que deseas eliminar este producto?")
            builder.setPositiveButton("Sí") { dialog, which ->
                // Elimina el producto de la base de datos
                deleteProduct(key.toString())

                Toast.makeText(
                    this,
                    key.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                // Después de eliminar, puedes cerrar esta actividad o realizar otras acciones necesarias
                finish()
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                // Si el usuario cancela, simplemente cierra el diálogo
                dialog.dismiss()
            }
            builder.show()
        }

        binding.editBtn.setOnClickListener{
            val intent = Intent(this, AddProductActivity::class.java)
            intent.putExtra("isEdit", true)
            intent.putExtra("keyProduct", key)
            intent.putExtra("object", item)
            startActivity(intent)
        }

        managmentCart = ManagmentCart(this)
        getBundles();
        banners()
        setupViewPager()
    }

    fun deleteProduct(productId: String) {
        val database = Firebase.database
        val usersRef = database.getReference("Users")

        // Suponiendo que tengas el ID del usuario y el ID del producto
        val userId = "UserID_1"
        val productIdToDelete = productId

        // Referencia al producto que deseas eliminar
        val productRef = usersRef.child(userId).child("products").child(productIdToDelete)

        // Eliminar el producto de la base de datos
        productRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Producto eliminado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun banners() {
        //MOSTRAR LAS IMAGENES DE LOS PRODUCTOS EN UN SLIDER
        var sliderItems = ArrayList<SliderItems>()

        for (i in item.picUrl.indices) {
            sliderItems.add(SliderItems(item.picUrl[i]))
        }

        binding.viewpageSlider.adapter = SliderAdapter(sliderItems, binding.viewpageSlider)
        binding.viewpageSlider.clipToPadding = false
        binding.viewpageSlider.clipChildren = false
        binding.viewpageSlider.offscreenPageLimit = 3
        binding.viewpageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    private fun setupViewPager(){
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val tab1: DescriptionFragment = DescriptionFragment()
        val tab2: ReviewFragment = ReviewFragment()
        val tab3: SoldFragment = SoldFragment()

        val bundle1: Bundle = Bundle()
        val bundle2: Bundle = Bundle()
        val bundle3: Bundle = Bundle()

        bundle1.putString("description", item.description)
        tab1.arguments = bundle1
        tab2.arguments = bundle2
        tab3.arguments = bundle3

        adapter.addFrag(tab1, "Descriptions")
        adapter.addFrag(tab2, "Reviews")
        adapter.addFrag(tab3, "Sold")

        binding.viewpager.adapter = adapter
        binding.tablayout.setupWithViewPager(binding.viewpager)

    }

    private fun getBundles() {

        item = intent.getSerializableExtra("object") as? itemsDomain ?: return
        binding.titleTxt.text = item.title
        binding.priceTxt.text = "$"+item.price
        binding.ratingBar.rating = item.rating.toFloat()
        binding.addTocartBtn.setOnClickListener { v ->
            item.numberinCart = numberOrder
            managmentCart.insertFood(item)
        }
        binding.backBtn.setOnClickListener{ v ->
            finish()
        }

    }

    class ViewPagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm){
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        public fun addFrag(fragment: Fragment, title: String){
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

    }

}