/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, { useCallback, useEffect } from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  useColorScheme,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import {
  createRecorder,
  startRecorder,
  stopRecorder,
  pauseRecorder,
  resumeRecorder,
} from 'react-native-audio-recorder-with-waveform';

import { Colors } from 'react-native/Libraries/NewAppScreen';

const App = () => {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const handleStartRecorder = useCallback(() => {
    startRecorder('sample.wav');
  }, []);
  const handlePauseRecorder = useCallback(() => {
    stopRecorder();
  }, []);
  const handleResumeRecorder = useCallback(() => {
    pauseRecorder();
  }, []);
  const handleStopRecorder = useCallback(() => {
    resumeRecorder();
  }, []);

  useEffect(() => {
    createRecorder();
  }, []);

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <View style={styles.container}>
        {/* <View style={styles.waveContainer}>
          {!isHide && (
            <AudioWaveformView.Recorder
              style={{ width: 400, height: 200 }}
              gap={3}
              waveWidth={3}
              radius={3}
              minHeight={1}
              gravity={'center'}
              barPgColor={'#FF0000'}
              barBgColor={'#0000FF'}
              onError={({ nativeEvent: { error } }) => {
                console.log({ error });
              }}
              onBuffer={({
                nativeEvent: { maxAmplitude, bufferData, readCount },
              }) => {
                //  const chunk = Buffer.from(bufferData, 'base64');
                //   console.log({ bufferData: chunk, maxAmplitude,  })
              }}
              onFFmpegState={({ nativeEvent: { ffmpegState } }) => {
                console.log({ ffmpegState });
              }}
              onFinished={({ nativeEvent: { file, duration } }) => {
                console.log({ file, duration });
                refPlayer?.current?.setSource(file, false);
              }}
              onProgress={({ nativeEvent: { currentTime, maxTime } }) => {
                console.log({ currentTime, maxTime });
              }}
              onRecorderState={({ nativeEvent: { recordState } }) => {
                console.log({ recordState });
              }}
              onSilentDetected={({ nativeEvent: { time } }) => {
                console.log({ time });
              }}
            />
          )}
        </View>
        <View style={styles.waveContainer}>
          {!isHide && (
            <AudioWaveformView.Player
              ref={refPlayer}
              style={{ width: 400, height: 200 }}
              gap={3}
              waveWidth={6}
              radius={3}
              minHeight={1}
              gravity={'center'}
              playbackSpeed={playbackSpeed}
              barPgColor={'#FF0000'}
              barBgColor={'#00FF00'}
              onSeekChange={({ nativeEvent: { progress, fromUser } }) => {
                console.log({ progress, fromUser });
              }}
              onError={({ nativeEvent: { error } }) => {
                console.log({ error });
              }}
              onPlayerState={({ nativeEvent: { playState } }) => {
                console.log({ playState });
              }}
              onProgress={({ nativeEvent: { currentTime, maxTime } }) => {
                console.log({ currentTime, maxTime });
              }}
              onLoadAmps={({ nativeEvent: { loadAmps } }) => {
                console.log({ loadAmps });
              }}
              onAmpsState={({ nativeEvent: { ampsState } }) => {
                console.log({ ampsState });
              }}
            />
          )}
        </View> */}
        <Text style={styles.optionText}>Recorder</Text>
        <View style={styles.boxContainer}>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={handleStartRecorder}
            style={styles.box}
          >
            <Text style={styles.boxText}>Start</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={handlePauseRecorder}
            style={styles.box}
          >
            <Text style={styles.boxText}>Pause</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={handleResumeRecorder}
            style={styles.box}
          >
            <Text style={styles.boxText}>Resume</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={handleStopRecorder}
            style={styles.box}
          >
            <Text style={styles.boxText}>Stop</Text>
          </TouchableOpacity>
        </View>
        {/* <Text style={styles.optionText}>Player</Text>
        <View style={styles.boxContainer}>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => {
              refPlayer.current?.startPlaying();
            }}
            style={styles.box}
          >
            <Text style={styles.boxText}>Start</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => {
              refPlayer.current?.pausePlaying();
            }}
            style={styles.box}
          >
            <Text style={styles.boxText}>Pause</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => {
              refPlayer.current?.resumePlaying();
            }}
            style={styles.box}
          >
            <Text style={styles.boxText}>Resume</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => {
              refPlayer.current?.stopPlaying();
            }}
            style={styles.box}
          >
            <Text style={styles.boxText}>Stop</Text>
          </TouchableOpacity>
          <TouchableOpacity
            activeOpacity={0.8}
            onPress={() => {
              if (playbackSpeed === 1.0) {
                setPlaybackSpeed(1.25);
              } else if (playbackSpeed === 1.25) {
                setPlaybackSpeed(2);
              } else {
                setPlaybackSpeed(1);
              }
            }}
            style={styles.box}
          >
            <Text style={styles.boxText}>
              {playbackSpeed === 1.25 ? 1.5 : playbackSpeed}x
            </Text>
          </TouchableOpacity>
        </View> */}
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  waveContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: 10,
  },
  box: {
    width: 80,
    height: 30,
    borderRadius: 15,
    backgroundColor: 'green',
    borderColor: 'blue',
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  boxText: {
    fontSize: 14,
    color: 'white',
  },
  boxContainer: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 10,
  },
  optionText: {
    fontSize: 28,
    color: 'red',
    paddingVertical: 10,
  },
});

export default App;
