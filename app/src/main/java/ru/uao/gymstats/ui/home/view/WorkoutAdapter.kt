package com.malkinfo.editingrecyclerview.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import ru.uao.gymstats.R
import ru.uao.gymstats.ui.home.data.Workout

class WorkoutAdapter(val c:Context,val workoutList:ArrayList<Workout>):RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>()
{
    inner class WorkoutViewHolder(v:View):RecyclerView.ViewHolder(v){
        var workoutInfo:EditText = v.findViewById(R.id.workoutInfo)
        var count:EditText = v.findViewById(R.id.count)
        var weight:EditText = v.findViewById(R.id.weight)
        var mMenus:ImageView = v.findViewById(R.id.mMenus)

        init {
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(v:View) {
            val position = workoutList[adapterPosition]
            val popupMenus = PopupMenu(c,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val v = LayoutInflater.from(c).inflate(R.layout.add_item,null)
                        val workoutInfo = v.findViewById<EditText>(R.id.workoutInfo)
                        val count = v.findViewById<EditText>(R.id.count)
                        val weight = v.findViewById<EditText>(R.id.weight)
                        AlertDialog.Builder(c)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                    dialog,_->
                                position.workoutInfo = workoutInfo.text.toString()
                                position.count = count.text.toString().toInt()
                                position.weight = weight.text.toString().toFloat()

                                notifyDataSetChanged()
                                Toast.makeText(c,"User Information is Edited",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()

                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.delete->{
                        /**set delete*/
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this Information")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                workoutList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(c,"Deleted this Information",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.fragment_home,parent,false)
        return WorkoutViewHolder(v)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val newList = workoutList[position]
        holder.workoutInfo.setText(newList.workoutInfo)
        holder.count.setText(newList.count.toString())
        holder.weight.setText(newList.weight.toString())
    }

    override fun getItemCount(): Int {
        return  workoutList.size
    }
}