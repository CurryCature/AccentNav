//
//  AudioRecord.swift
//  Acucent iOS
//
//  Created by Turja Das on 2024-04-25.
//

import SwiftUI
import AVFoundation

class AudioRecorder: NSObject, ObservableObject, AVAudioRecorderDelegate {
    var audioRecorder: AVAudioRecorder!
    @Published var isRecording = false

    func startRecording() {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(.playAndRecord, options: .defaultToSpeaker)
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation)

            let documentPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            let audioFilename = documentPath.appendingPathComponent("recording.wav")

            let settings = [
                AVFormatIDKey: Int(kAudioFormatLinearPCM),
                AVSampleRateKey: 44100,
                AVNumberOfChannelsKey: 1,
                AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
            ]

            audioRecorder = try AVAudioRecorder(url: audioFilename, settings: settings)
            audioRecorder.delegate = self
            audioRecorder.record()

            isRecording = true
        } catch {
            print("Error starting recording: \(error.localizedDescription)")
        }
    }

    func stopRecording() {
        audioRecorder.stop()
        isRecording = false
    }
}

