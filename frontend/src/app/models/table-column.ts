import { TableAction } from "./tabla-action";

export interface TableColumn {
  key: string;
  label: string;
  isAction?: boolean;
  isTag?: boolean;
  actions?: TableAction[];
}
