/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softsquared.template.kotlin.src.bluetoothlechat.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.softsquared.template.kotlin.R
import com.softsquared.template.kotlin.databinding.FragmentBluetoothChatBinding
import com.softsquared.template.kotlin.src.bluetoothlechat.Classifier
import com.softsquared.template.kotlin.src.bluetoothlechat.CurrResultActivity
import com.softsquared.template.kotlin.src.bluetoothlechat.bluetooth.ChatServer
import com.softsquared.template.kotlin.src.bluetoothlechat.bluetooth.Message
import com.softsquared.template.kotlin.src.bluetoothlechat.gone
import com.softsquared.template.kotlin.src.bluetoothlechat.visible
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "BluetoothChatFragment"
@SuppressLint("StaticFieldLeak")
var cls: Classifier? = null
// var cls: Classifier? = null
class BluetoothChatFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback

    private var connected_once = false
    private var stop_clicked = false
    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    /*?????? ????????? ??????*/
    private var data: String = ""

    /*????????? ?????? ???????????? ??????*/
    private var time: String? = null
    private var now = Date(System.currentTimeMillis()) // ?????? ?????? ?????? (Date)

    /* ?????? */

    private val brushStartDate = SimpleDateFormat("yyyy??? MM??? dd???", Locale.KOREA)
    private val brushStartTime = SimpleDateFormat("HH??? mm??? ss???", Locale.KOREA)
    private val tempDate = brushStartDate.format(now) // ??????????????????
    private val tempTime = brushStartTime.format(now) // ??????????????????

    /* ?????? */

    private var hour = now.hours // ?????? ???????????? (Int)
    private val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

    /*?????? ?????? ??????(??????, ????????????, ??????)*/
    private var tagCount = IntArray(17)// 0, 0, 0 ,0 ..., 0
    private var beforeTime: Long = 0
    private var afterTime: Long = 0
    private var secDiffTime: Long = 0
    private var score: Double = 0.0

    private var rightUpper= 0.0 // ????????? ????????? - TAG 1, 7, 13 => 1
    private var frontUpper= 0.0 // ?????? ????????? - TAG 2, 8 => 2
    private var leftUpper= 0.0 // ?????? ????????? - TAG 3, 9, 14 => 3
    private var leftBottom= 0.0 // ?????? ????????? - TAG 4, 10, 15 => 4
    private var frontBottom= 0.0 // ?????? ????????? - TAG 5, 11 => 5
    private var rightBottom= 0.0 // ????????? ????????? - TAG 6, 12, 16 => 6
    private var feedbackScore = Array(6, {0.0})
    private var resultArray = ArrayList<Int>()
    /*??????*/
    private var tagNow: Int = 0
    private var tagResult: String? = ""

    /*???*/
    private var linAccQueue: Queue<String> = LinkedList<String>()
    private var gyQueue: Queue<String> = LinkedList<String>()
    private var gravQueue: Queue<String> = LinkedList<String>()

    private val input = Array(1) { Array<FloatArray>(20) { FloatArray(9) } }

    val linAccX = FloatArray(20)
    val linAccY = FloatArray(20)
    val linAccZ = FloatArray(20)
    val accX = FloatArray(20)
    val accY = FloatArray(20)
    val accZ = FloatArray(20)
    val gyX = FloatArray(20)
    val gyY = FloatArray(20)
    val gyZ = FloatArray(20)
    val gravX = FloatArray(20)
    val gravY = FloatArray(20)
    val gravZ = FloatArray(20)


    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                chatWith(device)
                connected_once = true
                beforeTime = System.currentTimeMillis() // start ????????? ??? ?????? ?????? ??????
                // ?????? ?????? ?????? ?????? ?????? ?????? ?????? ??????
            }
            is DeviceConnectionState.Disconnected -> {
                // ?????? ?????? ??? ?????? ?????????
                if(connected_once){
                    //?????????(??? ????????? ????????? ????????? ????????? ????????????)
                    ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
                }
                // ??? ???????????????
                else{
                    showDisconnected()
                }

            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    //????????? ?????? ?????????, ?????? ???????????? ?????? ????????????.
    private val messageObserver = Observer<Message> { message ->
        Log.d(TAG, "Have message ${message.text}")
        if(message.text.contains("START")){
            beforeTime = System.currentTimeMillis()
            Toast.makeText(context,"START", Toast.LENGTH_LONG).show()
        }
        else if(message.text.contains("[STOP]")){
            stop_clicked = true
            afterTime = System.currentTimeMillis()
            Toast.makeText(context,"STOP", Toast.LENGTH_LONG).show()
        }
        else{
            //Log.d(TAG, "Input size: " + input.size)
            data =  message.text // ?????? 3?????? ???????????? ?????????
            // ?????? ????????? ?????????
            val getLine = data.split(",") // 3?????? ??????

            if((linAccQueue.size>20) && (gyQueue.size>20) && (gravQueue.size>20)){ // ????????? ????????? ?????? ???????????? ?????????.
                for(i in 0 until 20){
                    val linAcc = linAccQueue.poll().split(";")
                    linAccX[i] = linAcc[1].toFloat()
                    linAccY[i] = linAcc[3].toFloat()
                    linAccZ[i] = linAcc[5].toFloat()

                    val gy = gyQueue.poll().split(";")
                    gyX[i] = gy[1].toFloat()
                    gyY[i] = gy[3].toFloat()
                    gyZ[i] = gy[5].toFloat()

                    val grav = gravQueue.poll().split(";")
                    gravX[i] = grav[1].toFloat()
                    gravY[i] = grav[3].toFloat()
                    gravZ[i] = grav[5].toFloat()
                }
                // 2. ????????? ????????? output ????????? ?????????
                for (i in 0 until 20){
                    // ?????? ???????????? ?????? ???????????? ??????
                    accX[i] = linAccX[i] + gravX[i]
                    accY[i] = linAccY[i] + gravY[i]
                    accZ[i] = linAccZ[i] + gravZ[i]

                    input[0][i][0] = accX[i] // output[0][0] = accX[0], output[1][1] = accX[1]
                    input[0][i][1] = accY[i]
                    input[0][i][2] = accZ[i]
                    input[0][i][3] = gyX[i]
                    input[0][i][4] = gyY[i]
                    input[0][i][5] = gyZ[i]
                    input[0][i][6] = gravX[i]
                    input[0][i][7] = gravY[i]
                    input[0][i][8] = gravZ[i]
                }
                Log.d(TAG, "?????????: " + input.contentDeepToString())
                tagNow = cls!!.classify(input) + 1//as Int // ????????? ?????? ??? ????????? ????????? ?????? ??????
                Log.d(TAG, "tagNow: $tagNow")
                binding.txtTagNow.text = tagNow.toString()
                tagResult = tagResult + tagNow + ", "
                binding.txtTagResult.text = tagResult
                getTagData(tagNow)
                // ??? ?????? ?????? ??????
                for(i in getLine.indices){
                    if(getLine[i].contains("L[2]")){
                        linAccQueue.add(getLine[i]) // ACC 1???
                    }
                    else if(getLine[i].contains("Y[2]")){
                        gyQueue.add(getLine[i])// GY 1???
                    }
                    else if(getLine[i].contains("G[2]")){
                        gravQueue.add(getLine[i])// GRAV 1???
                    }
                }
            }
            else{ // ?????? ???????????? ????????? ????????????
                // ??? ?????? ?????? ??????
                for(i in getLine.indices){
                    if(getLine[i].contains("L[2]")){
                        linAccQueue.add(getLine[i]) // ACC 1???
                    }
                    else if(getLine[i].contains("Y[2]")){
                        gyQueue.add(getLine[i])// GY 1???
                    }
                    else if(getLine[i].contains("V[2]")){
                        gravQueue.add(getLine[i])// GRAV 1???
                    }
                }
            }
        }
    }

    private fun getTagData(tag: Int){
        tagCount[tag]+= 1
    }
    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        showDisconnected() // ??????

        binding.connectDevices.setOnClickListener {
            findNavController().navigate(R.id.action_find_new_device) // Device List Fragment
        }
        cls = context?.let { Classifier(it) }
        try {
            cls?.init()
        } catch (ioe: IOException) {
            Log.d("TagClassifier", "fail to init Classifier", ioe)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        this.requestPermissions(permissions, 5)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
        time = timestamp.format(now)
    }

    override fun onDestroyView() {
        cls?.finish()
        super.onDestroyView()
        _binding = null
    }
    private fun chatWith(device: BluetoothDevice) {
        binding.connectedContainer.visible()
        binding.notConnectedContainer.gone()
        var chattingWithString =""
        try {
            chattingWithString = resources.getString(R.string.chatting_with_device, device.name)
        }
        catch (e: SecurityException){
            e.printStackTrace()
        }
        binding.connectedDeviceName.text = chattingWithString

        binding.stopBtn.setOnClickListener {
            if(!stop_clicked){ // ???????????? ???????????? STOP ???????????? ?????? ?????? ?????? ??????
                afterTime = System.currentTimeMillis() // ???????????? ?????? ????????? ?????? ?????? ?????? ???????????? ??????
            }
            secDiffTime = (afterTime - beforeTime)/1000;
            score = scoring(tagCount)
            for(i in 0..5){
                if(feedbackScore[i] < 50){
                    resultArray.add(i)
                }
            }

            // ?????? ????????? ????????? ?????? ??? ??????
            val returnIntent = Intent(activity, CurrResultActivity::class.java) // ?????? ??????????????? ?????? ?????????
            // ??? ??????
            returnIntent.putExtra("startDate",tempDate) // ?????? ?????? ?????? (String)
            returnIntent.putExtra("startTime",tempTime) // ?????? ?????? ?????? (String)
            returnIntent.putExtra("brushing_time",secDiffTime) // ??????????????? ?????? ?????? (Long)
            returnIntent.putExtra("score",score) // ?????? ?????? (Double)
            returnIntent.putExtra("feedback",resultArray) // ?????? ?????? (Double)

//            /* ?????? */
//            val DetailIntent = Intent(getActivity(), DateDetailActivity::class.java) // ????????? ??????????????? ??????
//            // ??? ??????
//            DetailIntent.putExtra("startDate",tempDate) // ?????? ?????? ?????? (String)
//            DetailIntent.putExtra("startTime",tempTime) // ?????? ?????? ?????? (String)
//            DetailIntent.putExtra("brushing_time",secDiffTime) // ??????????????? ?????? ?????? (Long)
//            DetailIntent.putExtra("score",score) // ?????? ?????? (Double)

            /* ?????? */

            activity?.let { it1 -> ActivityCompat.finishAffinity(it1) } // ?????? ???????????? ??????

            startActivity(returnIntent)// ?????? ??????
            //startActivity(DetailIntent)// ?????? ??????
        }
    }
    private fun showDisconnected() {
        hideKeyboard()
        binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_find_new_device) // Device List Fragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun scoring(array: IntArray): Double{
        var score = 0.0
        // ?????? ??? ????????? 5??? ???????????? 100???????????? ????????????.
        for(i in 0..15){
            score += (20 * array[i])
            if((i==0)||(i==6)||(i==12)){
                rightUpper += score
            }
            else if((i==1)||(i==7)){
                frontUpper += score
            }
            else if((i==2)||(i==8)||(i==13)){
                leftUpper += score
            }
            else if((i==3)||(i==9)||(i==14)){
                leftBottom += score
            }
            else if((i==4)||(i==10)){
                frontBottom += score
            }
            else if((i==5)||(i==11)||(i==15)){
                rightBottom += score
            }
            print("score: "+ (20 * i))
        }
        rightUpper /= 3
        feedbackScore[0] = rightUpper
        frontUpper /= 2
        feedbackScore[1] = frontUpper
        leftUpper /= 3
        feedbackScore[2] = leftUpper
        leftBottom /= 3
        feedbackScore[3] = leftBottom
        frontBottom /= 2
        feedbackScore[4] = frontBottom
        rightBottom /= 3
        feedbackScore[5] = rightBottom

        score /= 16
        print("total: $score")
        return score
    }
}