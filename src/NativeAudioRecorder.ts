// @ts-ignore
import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getConstants: () => {};

  setupRecorder: (
    sourceMode?: string,
    isFFmpegMode?: boolean,
    isDebug?: boolean,
    subscriptionDurationInMilliseconds?: number,
    audioSource?: number,
    audioEncoding?: number,
    frequency?: number,
    bitRate?: number,
    samplingRate?: number,
    mono?: boolean
  ) => void;

  startRecording: (filePath?: string) => void;
  pauseRecording: () => void;
  resumeRecording: () => void;
  stopRecording: () => void;
  cancelRecording: () => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AudioRecorder');
