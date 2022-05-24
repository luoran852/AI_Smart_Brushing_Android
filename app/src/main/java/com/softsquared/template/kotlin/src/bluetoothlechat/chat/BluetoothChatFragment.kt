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

class BluetoothChatFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback

    var connected_once = false
    var stop_clicked = false
    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    /*워치 데이터 저장*/
    private var output: String = ""
    private var cnt: Int = 0

    /*양치한 시간 저장하는 변수*/
    private var date: String? = null
    private var time: String? = null
    private var now = Date(System.currentTimeMillis())
    val pathFormat_date = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
    val pathFormat_time = SimpleDateFormat("HH시 mm분 ss초", Locale.KOREA)

    /*양치 결과 계산(태그, 소요시간, 점수)*/
    var tagCount = arrayOf<Int>(16, 0)// 0, 0, 0 ,0 ..., 0
    var beforeTime: Long = 0
    var afterTime: Long = 0
    var secDiffTime: Long = 0
    var score: Double = 0.0

    /*태그*/
    var tagNow: Int = 0
    var tagResult: String? = ""

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                chatWith(device)
                connected_once = true
            }
            is DeviceConnectionState.Disconnected -> {
                // 전에 연결 된 적이 있으면
                if(connected_once){
                    //재연결(이 코드가 재연결 코드가 맞는지 모르겠음)
                    ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
                }
                // 첫 연결이라면
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
    //자기가 보낸 메세지, 받은 메세지를 모두 처리한다.
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
            //output = "" // 초기화
//            when{
//                !isExternalStorageWritable() -> Toast.makeText(this,"외부 저장장치 없음", Toast.LENGTH_LONG).show()
//
//                else -> {
//                    val pathFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS", Locale.KOREA)
//                    time = pathFormat.format(date)
//                    val filename = time + ".txt"
//                    //외부저장소에 저장
//                    output?.let { it1 -> saveToExternalStorage(it1,filename) }
//                    System.out.println(output)
//                    Toast.makeText(this,"저장되었습니다", Toast.LENGTH_LONG).show()
//                    output = "" // 초기화
//                }
//            }
        }
        else{
            if(cnt == 20){ // 행 20개, 열 9개가 한 세트임
                val preprocessedInput = preprocess(output)// 전처리 함수가 파싱한 데이터 리턴
                // 모델에 입력 값 넣으면 모델이 태그 리턴
                tagNow = 1// 모델에서 리턴해야 함!!
                getTagData(tagNow)
                binding.txtTagNow.text = tagNow.toString()
                tagResult += tagNow
                binding.txtTagResult.text = tagResult
                output = ""
                cnt = 0
            }
            else{
                cnt+=1 // 카운트를 하나 늘려준다
                output += ( message.text ) // 워치로부터 받은 데이터를 모두 output에 저장한다
            }

        }
    }
    fun preprocess(input: String): String {
        val output: String = ""
        return output
    }
    fun getTagData(tag: Int){
        tagCount[tag]++
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

        showDisconnected() // 필수

        binding.connectDevices.setOnClickListener {
            findNavController().navigate(R.id.action_find_new_device) // Device List Fragment
        }
        try {
            context?.let { Classifier(it) }?.init()
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
        date = pathFormat_date.format(now)
        time = pathFormat_time.format(now)
    }

    override fun onDestroyView() {
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
            if(!stop_clicked){ // 마지막에 워치에서 STOP 메세지를 전달 받지 못한 경우
                afterTime = System.currentTimeMillis() // 사용자가 양치 종료를 누른 것을 종료 기점으로 한다
            }
            secDiffTime = (afterTime - beforeTime)/1000;
            score = scoring(tagCount)
            //Toast.makeText(this,"Time: "+ secDiffTime +"초" + " score: " + score, Toast.LENGTH_LONG).show()
            // 결과 돌려줄 인텐트 생성 후 저장
            val returnIntent = Intent(getActivity(), CurrResultActivity::class.java) // 결과 액티비티로 이동 해야함
            // 값 담기
            returnIntent.putExtra("time",time)
            returnIntent.putExtra("date",date)
            returnIntent.putExtra("brushing_time",secDiffTime)
            returnIntent.putExtra("score",score)

            getActivity()?.let { it1 -> ActivityCompat.finishAffinity(it1) } // 모든 액티비티 종료

            startActivity(returnIntent)// 최종 전달
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

    private fun scoring(array: Array<Int>): Double{
        var score = 0.0
        // 구역 당 태그가 5개 들어와야 100점이라고 가정한다.
        for(i in array){
            score += (20 * i)
            print("score: "+ (20 * i))
        }
        score /= 16
        print("total: $score")
        return score
    }
}