package com.example.helloworld.ui

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.helloworld.databinding.FragmentEightBinding
import java.util.Locale

class EightFragment : Fragment() {
    private var _binding: FragmentEightBinding? = null
    private val binding get() = _binding!!

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext().applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ttsReady = true
            }
        }

        binding.btnSayEight.setOnClickListener {
            if (ttsReady) {
                tts?.speak("8", TextToSpeech.QUEUE_FLUSH, null, "eight")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
        _binding = null
    }
}
