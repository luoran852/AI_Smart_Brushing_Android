package com.softsquared.template.kotlin.src.bluetoothlechat

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel


class Classifier {

    val context: Context
    private val MODEL_NAME: String = "saved_model.tflite"
    var interpreter: Interpreter? = null
    //모델의 입력 크기 변수 선언
    var modelInputWidth: Int = 0
    var modelInputHeight:Int = 0
    var modelInputChannel:Int = 0
    //모델 출력 클래스 수를 담을 변수 선언
    var modelOutputClasses:Int = 0

    constructor(context: Context) {
        this.context = context
    }

    @Throws(IOException::class)
    private fun loadModelFile(modelName: String): ByteBuffer? {
        // am: Asset Manager - assets 폴더에 저장된 리소스에 접근하기 위한 기능 제공
        val am = context.assets
        // am의 openFd() 함수에 tflite 파일명을 전달하면 AssetFileDescriptor를 얻는다.
        val afd = am.openFd(modelName)
        /*
        * afd의 getFileDescriptor() 함수를 이용하면 읽은 파일의 FileDescriptor를 얻는다.
        * 이를 이용하면 해당 파일의 읽기/쓰기가 가능하다.
        * FileInputStrram 생성자에 FileDescriptor를 매개변수로 전달하면 FileInputStream을 얻는다.
        * */
        val fis = FileInputStream(afd.fileDescriptor)
        /* read() 함수로 바로 파일을 읽을 수도 있지만
        * 성능을 위해 getChannel() 함수로 반환받은 FileChannel을 이용한다.*/
        val fc = fis.channel
        val startOffset = afd.startOffset
        val declaredLength = afd.declaredLength
        /* FileChannel의 map() 함수에 길이와 오프셋을 전달하면
        * ByteBuffer 클래스를 상속한 MappedByteBuffer 객체를 반환한다.
        * 마침내 tflite 파일을 ByteBuffer 형으로 읽어오는데 성공한다.
        * */
        return fc.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /*모델 초기화 관련 동작 구현*/
    @Throws(IOException::class)
    fun init() {
        val model = loadModelFile(MODEL_NAME) // 모델을 읽는다
        model!!.order(ByteOrder.nativeOrder()) // 모델을 담은 ByteBuffer 객체
        /*Interpreter: 모델에 데이터를 입력하고 추론 결과를 전달 받을 수 있는 클래스
        * ByteBuffer 포맷으로 얻은 model을 Interpreter 클래스의 생성자에 전달하여
        * interpreter 인스턴스를 만든다. */
        interpreter = Interpreter(model)
        initModelShape()
    }

    /*모델의 입출력 크기 계산 함수 정의*/
    private fun initModelShape() {
        val inputTensor = interpreter!!.getInputTensor(0)
        val inputShape = inputTensor.shape()
        modelInputChannel = inputShape[0]
        modelInputWidth = inputShape[1]
        modelInputHeight = inputShape[2]

        val outputTensor = interpreter!!.getOutputTensor(0)
        val outputShape = outputTensor.shape()
        modelOutputClasses = outputShape[1]
        Log.d("Classifier", "modelOutputClasses: $modelOutputClasses")
        Log.d("Classifier", "input 채널: $modelInputChannel")
        Log.d("Classifier", "Width 크기(9예상): $modelInputWidth")
        Log.d("Classifier", "Height 크기(20예상): $modelInputHeight")
    }

    /*양치 구역 분석 모델의 추론*/
    open fun classify(input: Array<Array<FloatArray>>): Int {
        //val result = FloatArray(modelOutputClasses) // 16
        val result = Array(1) { FloatArray(modelOutputClasses) }
        interpreter!!.run(input, result)
        Log.d("result 배열: ", result.contentDeepToString())
        return argmax(result)
    }

    /*추론 결과 해석*/
    private fun argmax(array: Array<FloatArray>): Int {
        var argmax = -1 // 가장 확률이 높은 클래스: -1로 초기화
        var max = 0.0 // 그 클래스의 확률: Class 1 확률로 초기화
        Log.d("array.size(16이어야 하는데 설마 1?): ", array.size.toString())
        for (i in 0 until 16) { // i= 1..15
            val f = array[0][i] // Class 2~16까지의 확률을 비교
            Log.d("Class[$i] 확률 값: ", f.toString())
            if (f > max) {
                argmax = i
                max = f.toDouble() // max 확률 업데이트
            }
        }
        return argmax
    }
    public fun finish(){
        interpreter?.close()
    }
}