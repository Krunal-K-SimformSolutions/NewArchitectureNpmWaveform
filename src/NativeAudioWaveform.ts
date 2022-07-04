// @ts-ignore
import type { ViewProps } from 'ViewPropTypes';
import type { HostComponent } from 'react-native';
// @ts-ignore
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
// @ts-ignore
import type { Float } from 'react-native/Libraries/Types/CodegenTypes';

export interface NativeProps extends ViewProps {
  // add other props here
  visibleProgress?: Float;
  progress?: Float;
  maxProgress?: Float;
  waveWidth?: Float;
  gap?: Float;
  minHeight?: Float;
  radius?: Float;
  gravity?: string; //'top' | 'center' | 'bottom';
  barBgColor?: string;
  barPgColor?: string;
  forRecorder?: boolean;
}

export default codegenNativeComponent<NativeProps>(
  'WaveformView'
) as HostComponent<NativeProps>;
