export interface DialogField {
  type: string;
  label: string;
  name: string;
  value?: any;
  options?: { label: string; value: any }[];
}
