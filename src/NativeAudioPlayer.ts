// @ts-ignore
import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getConstants: () => {};

  setupPlayer: (
    isDebug?: boolean,
    subscriptionDurationInMilliseconds?: number
  ) => void;

  setupSource: (filePath?: string, isAmplitudaMode?: boolean) => void;
  startPlaying: () => void;
  pausePlaying: () => void;
  resumePlaying: () => void;
  stopPlaying: () => void;
  setPlaybackSpeed: (speed: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AudioPlayer');
