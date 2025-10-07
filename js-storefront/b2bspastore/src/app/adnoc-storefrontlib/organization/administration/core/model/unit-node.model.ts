/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

export interface B2BUnitNode {
  active?: boolean;
  children?: B2BUnitNode[];
  id?: string;
  name?: string;
  parent?: string;
  unitNodes?: B2BUnitNode[];
}

export interface B2BUnitTreeNode extends B2BUnitNode {
  expanded: boolean;
  depthLevel: number;
  count: number;
  uid: string;
}
